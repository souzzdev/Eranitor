package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.DistribuicaoMateriaDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Service
public class MetaService {

    private static final int META_MINIMA = 1;
    private static final int META_MAXIMA = 168;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Transactional
    public void setMetaSemanal(Long usuarioId, Integer horasTotal) {
        validarUsuarioId(usuarioId);
        validarMeta(horasTotal);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        usuario.setMetaSemanalHoras(horasTotal);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public List<DistribuicaoMateriaDTO> sugerirDistribuicao(Long usuarioId, Integer horasTotal) {
        validarUsuarioId(usuarioId);
        validarMeta(horasTotal);

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }

        List<Materia> materias = materiaRepository.findAllByUsuario_IdUsuarioAndAtivaTrue(usuarioId);

        if (materias.isEmpty()) {
            throw new IllegalStateException("O usuário não possui matérias ativas para distribuir as horas.");
        }

        materias.forEach(this::validarDificuldade);

        int somaDificuldades = materias.stream()
                .mapToInt(Materia::getDificuldade)
                .sum();

        List<Parcela> parcelas = new ArrayList<>();
        int horasDistribuidas = 0;

        for (Materia materia : materias) {
            int peso = materia.getDificuldade();

            long numerador = (long) horasTotal * peso;
            int horasBase = (int) (numerador / somaDificuldades);
            long resto = numerador % somaDificuldades;

            parcelas.add(new Parcela(materia, horasBase, resto));
            horasDistribuidas += horasBase;
        }

        int horasRestantes = horasTotal - horasDistribuidas;

        List<Parcela> porMaiorResto = parcelas.stream()
                .sorted(
                        Comparator.comparingLong(Parcela::resto)
                                .reversed()
                                .thenComparing(
                                        parcela -> parcela.materia.getIdMateria()
                                )
                )
                .toList();

        for (int i = 0; i < horasRestantes; i++) {
            Parcela parcela = porMaiorResto.get(i);

            int indice = parcelas.indexOf(parcela);

            parcelas.set(
                    indice,
                    new Parcela(
                            parcela.materia(),
                            parcela.horas() + 1,
                            parcela.resto()
                    )
            );
        }

        return parcelas.stream()
                .map(parcela -> new DistribuicaoMateriaDTO(
                        parcela.materia().getIdMateria(),
                        parcela.materia().getNome(),
                        parcela.materia().getDificuldade(),
                        parcela.horas()
                ))
                .sorted(
                        Comparator.comparing(
                                DistribuicaoMateriaDTO::nomeMateria,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .toList();
    }
    private void validarUsuarioId(Long usuarioId) {
        Objects.requireNonNull(
                usuarioId,
                "O ID do usuário deve ser positivo."
        );

        if (usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "O ID do usuário deve ser positivo."
            );
        }
    }

    private void validarMeta(Integer horasTotal) {
        Objects.requireNonNull(
                horasTotal,
                "A meta semanal não pode ser nula."
        );

        if (horasTotal <= META_MINIMA || horasTotal > META_MAXIMA) {
            throw new IllegalArgumentException(
                    "A meta semanal deve estar entre 1 e 168 horas."
            );
        }
    }

    public void validarDificuldade(Materia materia) {
        Integer dificuldade = materia.getDificuldade();

        if (dificuldade == null || dificuldade < 1 || dificuldade > 5) {
            throw new IllegalStateException(
                    "A matéria \"" + materia.getNome() + "\" deve possuir dificuldade entre 1 e 5"
            );
        }
    }

    private record Parcela(
            Materia materia,
            int horas,
            Long resto
    ) {}

}
